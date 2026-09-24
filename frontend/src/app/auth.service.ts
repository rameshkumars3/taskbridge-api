import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthUser, LoginResponse } from './models';

const TOKEN_KEY = 'taskbridge.accessToken';

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly user = signal<AuthUser | null>(this.readUser());
  constructor(private readonly http: HttpClient, private readonly router: Router) {}
  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/api/auth/login', { username, password }).pipe(tap(response => {
      const user = this.decode(response.accessToken);
      if (!user) throw new Error('The server returned an invalid authentication token.');
      this.user.set(user); localStorage.setItem(TOKEN_KEY, response.accessToken);
    }));
  }
  logout(): void { localStorage.removeItem(TOKEN_KEY); this.user.set(null); void this.router.navigateByUrl('/'); }
  token(): string | null {
    const token = localStorage.getItem(TOKEN_KEY); const user = this.user();
    if (!token || !user || user.expiresAt * 1000 <= Date.now()) { if (token) this.logout(); return null; }
    return token;
  }
  hasAuthority(authority: string): boolean { return this.user()?.authorities.includes(authority) ?? false; }
  private readUser(): AuthUser | null { const token = localStorage.getItem(TOKEN_KEY); return token ? this.decode(token) : null; }
  private decode(token: string): AuthUser | null {
    try {
      const payload = JSON.parse(this.decodeBase64(token.split('.')[1])) as Record<string, unknown>;
      const userId = Number(payload['userId']); const organisationId = String(payload['organisationId'] ?? ''); const username = String(payload['sub'] ?? ''); const expiresAt = Number(payload['exp']);
      const authorities = Array.isArray(payload['authorities']) ? payload['authorities'].map(String) : [];
      return userId > 0 && username && organisationId && expiresAt ? { username, userId, organisationId, authorities, expiresAt } : null;
    } catch { return null; }
  }
  private decodeBase64(value: string): string {
    const normalized = value.replace(/-/g, '+').replace(/_/g, '/');
    return decodeURIComponent(atob(normalized).split('').map(char => `%${(`00${char.charCodeAt(0).toString(16)}`).slice(-2)}`).join(''));
  }
}

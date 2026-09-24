import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from './auth.service';

function token(payload: Record<string, unknown>): string {
  const encoded = btoa(JSON.stringify(payload)).replace(/=/g, '').replace(/\+/g, '-').replace(/\//g, '_');
  return `header.${encoded}.signature`;
}

describe('AuthService', () => {
  let service: AuthService;
  let http: HttpTestingController;
  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])] });
    service = TestBed.inject(AuthService); http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => { http.verify(); localStorage.clear(); });

  it('stores a valid JWT and exposes trusted identity claims after login', () => {
    const accessToken = token({ sub: 'owner', userId: 42, organisationId: 'org-1', authorities: ['project:read'], exp: Math.floor(Date.now() / 1000) + 900 });
    service.login('owner', 'long-password').subscribe();
    const request = http.expectOne('/api/auth/login');
    expect(request.request.body).toEqual({ username: 'owner', password: 'long-password' });
    request.flush({ accessToken, tokenType: 'Bearer', expiresInSeconds: 900 });
    expect(service.user()).toEqual(jasmine.objectContaining({ username: 'owner', userId: 42, organisationId: 'org-1', authorities: ['project:read'] }));
    expect(service.token()).toBe(accessToken);
  });

  it('does not return an expired token', () => {
    const accessToken = token({ sub: 'owner', userId: 42, organisationId: 'org-1', authorities: [], exp: Math.floor(Date.now() / 1000) - 1 });
    localStorage.setItem('taskbridge.accessToken', accessToken);
    const fresh = TestBed.inject(AuthService);
    expect(fresh.token()).toBeNull();
    expect(fresh.user()).toBeNull();
  });
});

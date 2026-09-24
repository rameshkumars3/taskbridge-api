import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuditRecord, Notification, Page, Project, ProjectEventType, ProjectRequest } from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private readonly http: HttpClient) {}
  projects(page = 0, size = 8, teamId = ''): Observable<Page<Project>> { let params = new HttpParams().set('page', page).set('size', size); if (teamId.trim()) params = params.set('teamId', teamId.trim()); return this.http.get<Page<Project>>('/api/projects', { params }); }
  createProject(request: ProjectRequest): Observable<Project> { return this.http.post<Project>('/api/projects', request); }
  updateProject(id: number, request: ProjectRequest): Observable<Project> { return this.http.put<Project>(`/api/projects/${id}`, request); }
  deleteProject(id: number): Observable<void> { return this.http.delete<void>(`/api/projects/${id}`); }
  audit(id: number, eventType?: ProjectEventType): Observable<AuditRecord[]> { let params = new HttpParams(); if (eventType) params = params.set('eventType', eventType); return this.http.get<AuditRecord[]>(`/api/audit/${id}`, { params }); }
  notifications(userId: number): Observable<Notification[]> { return this.http.get<Notification[]>(`/api/notifications/${userId}`); }
  markRead(id: number): Observable<Notification> { return this.http.patch<Notification>(`/api/notifications/${id}/read`, {}); }
}

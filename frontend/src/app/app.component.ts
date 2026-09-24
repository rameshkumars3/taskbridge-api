import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { ApiService } from './api.service';
import { AuthService } from './auth.service';
import { ApiError, AuditRecord, Notification, Page, Project, ProjectEventType, ProjectRequest, ProjectStatus } from './models';

@Component({ selector: 'tb-root', standalone: true, imports: [CommonModule, FormsModule, ReactiveFormsModule], templateUrl: './app.component.html' })
export class AppComponent implements OnInit {
  readonly auth = inject(AuthService); private readonly api = inject(ApiService); private readonly fb = inject(FormBuilder);
  readonly statuses: ProjectStatus[] = ['DRAFT', 'ACTIVE', 'COMPLETED', 'ARCHIVED']; readonly eventTypes: ProjectEventType[] = ['PROJECT_CREATED', 'PROJECT_STATUS_CHANGED', 'PROJECT_DELETED', 'PROJECT_UPDATED', 'MILESTONE_REOPENED'];
  loginForm = this.fb.nonNullable.group({ username: ['', Validators.required], password: ['', [Validators.required, Validators.minLength(12)]] });
  projectForm = this.fb.nonNullable.group({ name: ['', [Validators.required, Validators.maxLength(120)]], description: ['', Validators.maxLength(2000)], teamId: ['', [Validators.required, Validators.maxLength(80)]], status: ['DRAFT' as ProjectStatus, Validators.required] });
  projects: Page<Project> = { content: [], totalElements: 0, totalPages: 0, number: 0, size: 8 }; selectedProject: Project | null = null; audit: AuditRecord[] = []; notifications: Notification[] = []; teamFilter = ''; activeView: 'overview' | 'activity' = 'overview'; busy = false; error = ''; notice = '';
  ngOnInit(): void { if (this.auth.user()) this.refresh(); }
  submitLogin(): void { if (this.loginForm.invalid) { this.loginForm.markAllAsTouched(); return; } this.busy = true; this.error = ''; this.auth.login(this.loginForm.controls.username.value.trim(), this.loginForm.controls.password.value).pipe(finalize(() => this.busy = false)).subscribe({ next: () => this.refresh(), error: error => this.showError(error) }); }
  logout(): void { this.auth.logout(); }
  refresh(page = 0): void { const user = this.auth.user(); if (!user) return; this.busy = true; this.error = ''; this.api.projects(page, 8, this.teamFilter).subscribe({ next: projects => { this.projects = projects; this.busy = false; }, error: error => { this.busy = false; this.showError(error); } }); this.api.notifications(user.userId).subscribe({ next: notifications => this.notifications = notifications, error: error => this.showError(error) }); }
  edit(project: Project): void { this.selectedProject = project; this.projectForm.setValue({ name: project.name, description: project.description ?? '', teamId: project.teamId, status: project.status }); this.activeView = 'overview'; }
  newProject(): void { this.selectedProject = null; this.projectForm.reset({ name: '', description: '', teamId: '', status: 'DRAFT' }); }
  saveProject(): void { if (this.projectForm.invalid) { this.projectForm.markAllAsTouched(); return; } const request = this.projectForm.getRawValue() as ProjectRequest; this.busy = true; this.notice = ''; this.error = ''; const call = this.selectedProject ? this.api.updateProject(this.selectedProject.id, request) : this.api.createProject(request); call.pipe(finalize(() => this.busy = false)).subscribe({ next: () => { this.notice = this.selectedProject ? 'Project updated.' : 'Project created.'; this.newProject(); this.refresh(this.projects.number); }, error: error => this.showError(error) }); }
  remove(project: Project): void { if (!confirm(`Delete ${project.name}? This cannot be undone.`)) return; this.busy = true; this.api.deleteProject(project.id).pipe(finalize(() => this.busy = false)).subscribe({ next: () => { this.notice = 'Project deleted.'; this.refresh(this.projects.number); }, error: error => this.showError(error) }); }
  showAudit(project: Project): void { this.selectedProject = project; this.activeView = 'activity'; this.api.audit(project.id).subscribe({ next: audit => this.audit = audit, error: error => this.showError(error) }); }
  openActivity(): void { this.activeView = 'activity'; const project = this.selectedProject ?? this.projects.content[0]; if (project) this.showAudit(project); }
  markRead(notification: Notification): void { if (notification.read) return; this.api.markRead(notification.id).subscribe({ next: updated => notification.read = updated.read, error: error => this.showError(error) }); }
  unreadCount(): number { return this.notifications.filter(notification => !notification.read).length; }
  can(permission: string): boolean { return this.auth.hasAuthority(permission); }
  formatDate(value: string): string { return new Intl.DateTimeFormat('en', { month: 'short', day: 'numeric', hour: 'numeric', minute: '2-digit' }).format(new Date(value)); }
  private showError(error: ApiError): void { this.error = error?.message || 'The request could not be completed. Check the API and try again.'; }
}

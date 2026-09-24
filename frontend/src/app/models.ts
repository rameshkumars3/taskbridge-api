export type ProjectStatus = 'DRAFT' | 'ACTIVE' | 'COMPLETED' | 'ARCHIVED';
export type ProjectEventType = 'PROJECT_CREATED' | 'PROJECT_STATUS_CHANGED' | 'PROJECT_DELETED' | 'PROJECT_UPDATED' | 'MILESTONE_REOPENED';

export interface Project {
  id: number;
  name: string;
  description: string | null;
  teamId: string;
  status: ProjectStatus;
}

export interface ProjectRequest {
  name: string;
  description: string;
  teamId: string;
  status: ProjectStatus;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface AuditRecord {
  id: number;
  organisationId: string;
  projectId: number;
  actorUserId: number;
  eventType: ProjectEventType;
  previousStatus: ProjectStatus | null;
  newStatus: ProjectStatus | null;
  message: string | null;
  createdAt: string;
  deduplicationKey: string;
}

export interface Notification {
  id: number;
  organisationId: string;
  userId: number;
  projectId: number;
  eventType: ProjectEventType;
  title: string;
  message: string;
  read: boolean;
  createdAt: string;
  readAt: string | null;
  deduplicationKey: string;
}

export interface LoginResponse { accessToken: string; tokenType: string; expiresInSeconds: number; }
export interface AuthUser { username: string; userId: number; organisationId: string; authorities: string[]; expiresAt: number; }
export interface ApiError { status?: number; error?: string; message?: string; timestamp?: string; }

import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ApiService } from './api.service';

describe('ApiService', () => {
  let service: ApiService;
  let http: HttpTestingController;
  beforeEach(() => { TestBed.configureTestingModule({ providers: [ApiService, provideHttpClient(), provideHttpClientTesting()] }); service = TestBed.inject(ApiService); http = TestBed.inject(HttpTestingController); });
  afterEach(() => http.verify());

  it('requests a paginated, team-filtered project list', () => {
    service.projects(2, 8, 'platform').subscribe();
    const request = http.expectOne(request => request.url === '/api/projects' && request.params.get('page') === '2' && request.params.get('size') === '8' && request.params.get('teamId') === 'platform');
    expect(request.request.method).toBe('GET'); request.flush({ content: [], totalElements: 0, totalPages: 0, number: 2, size: 8 });
  });

  it('sends the project contract without tenant or actor fields', () => {
    const body = { name: 'Launch', description: '', teamId: 'platform', status: 'DRAFT' as const };
    service.createProject(body).subscribe();
    const request = http.expectOne('/api/projects');
    expect(request.request.method).toBe('POST'); expect(request.request.body).toEqual(body); expect(request.request.body.organisationId).toBeUndefined(); expect(request.request.body.actorUserId).toBeUndefined(); request.flush({ id: 1, ...body });
  });

  it('marks only the requested notification as read', () => {
    service.markRead(17).subscribe();
    const request = http.expectOne('/api/notifications/17/read');
    expect(request.request.method).toBe('PATCH'); expect(request.request.body).toEqual({}); request.flush({ id: 17, read: true });
  });
});

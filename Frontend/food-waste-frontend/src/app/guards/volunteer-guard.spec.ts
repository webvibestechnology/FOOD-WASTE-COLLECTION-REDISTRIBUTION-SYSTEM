import { TestBed } from '@angular/core/testing';
import { CanActivateFn, provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

import { volunteerGuard } from './volunteer-guard';

describe('volunteerGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => volunteerGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideRouter([])]
    });
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});

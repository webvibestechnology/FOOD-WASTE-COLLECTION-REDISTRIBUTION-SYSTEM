import { TestBed } from '@angular/core/testing';
import { CanActivateFn, provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

import { donorGuard } from './donor-guard';

describe('donorGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => donorGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideRouter([])]
    });
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});

import { TestBed } from '@angular/core/testing';
import { CanActivateFn, provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

import { ngoGuard } from './ngo-guard';

describe('ngoGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => ngoGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideRouter([])]
    });
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});

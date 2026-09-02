import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { donorGuard } from './donor-guard';

describe('donorGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => donorGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});

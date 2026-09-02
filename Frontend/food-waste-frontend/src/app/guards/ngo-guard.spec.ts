import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { ngoGuard } from './ngo-guard';

describe('ngoGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => ngoGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});

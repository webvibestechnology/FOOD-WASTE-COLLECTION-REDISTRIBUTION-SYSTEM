import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { Pickup } from './pickup';

describe('Pickup', () => {
  let service: Pickup;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient()]
    });
    service = TestBed.inject(Pickup);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

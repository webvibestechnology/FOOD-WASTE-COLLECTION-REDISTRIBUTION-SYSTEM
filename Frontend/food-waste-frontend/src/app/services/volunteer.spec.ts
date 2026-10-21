import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { Volunteer } from './volunteer';

describe('Volunteer', () => {
  let service: Volunteer;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient()]
    });
    service = TestBed.inject(Volunteer);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

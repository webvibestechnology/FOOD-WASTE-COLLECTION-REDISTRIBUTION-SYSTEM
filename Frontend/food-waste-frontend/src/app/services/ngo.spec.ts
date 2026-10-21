import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { Ngo } from './ngo';

describe('Ngo', () => {
  let service: Ngo;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient()]
    });
    service = TestBed.inject(Ngo);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

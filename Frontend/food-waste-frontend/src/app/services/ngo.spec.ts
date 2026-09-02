import { TestBed } from '@angular/core/testing';

import { Ngo } from './ngo';

describe('Ngo', () => {
  let service: Ngo;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Ngo);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

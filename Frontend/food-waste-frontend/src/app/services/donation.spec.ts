import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { Donation } from './donation';

describe('Donation', () => {
  let service: Donation;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient()]
    });
    service = TestBed.inject(Donation);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

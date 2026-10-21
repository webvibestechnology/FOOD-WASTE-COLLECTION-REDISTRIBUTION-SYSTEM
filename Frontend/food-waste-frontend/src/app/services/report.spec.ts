import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { Report } from './report';

describe('Report', () => {
  let service: Report;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient()]
    });
    service = TestBed.inject(Report);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

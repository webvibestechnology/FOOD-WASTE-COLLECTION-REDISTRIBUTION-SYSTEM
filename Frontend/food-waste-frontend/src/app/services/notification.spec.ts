import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { Notification } from './notification';

describe('Notification', () => {
  let service: Notification;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient()]
    });
    service = TestBed.inject(Notification);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

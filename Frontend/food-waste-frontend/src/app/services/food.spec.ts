import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { Food } from './food';

describe('Food', () => {
  let service: Food;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient()]
    });
    service = TestBed.inject(Food);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

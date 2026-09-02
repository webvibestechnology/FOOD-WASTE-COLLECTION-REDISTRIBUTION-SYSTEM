import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FoodRequest } from './food-request';

describe('FoodRequest', () => {
  let component: FoodRequest;
  let fixture: ComponentFixture<FoodRequest>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FoodRequest],
    }).compileComponents();

    fixture = TestBed.createComponent(FoodRequest);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

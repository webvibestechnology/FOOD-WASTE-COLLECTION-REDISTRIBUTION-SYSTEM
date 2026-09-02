import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FoodDonation } from './food-donation';

describe('FoodDonation', () => {
  let component: FoodDonation;
  let fixture: ComponentFixture<FoodDonation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FoodDonation],
    }).compileComponents();

    fixture = TestBed.createComponent(FoodDonation);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

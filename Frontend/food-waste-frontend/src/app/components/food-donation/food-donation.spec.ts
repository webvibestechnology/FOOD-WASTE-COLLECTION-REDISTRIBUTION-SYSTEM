import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { FoodDonationComponent } from './food-donation';

describe('FoodDonationComponent', () => {
  let component: FoodDonationComponent;
  let fixture: ComponentFixture<FoodDonationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FoodDonationComponent],
      providers: [provideHttpClient(), provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(FoodDonationComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

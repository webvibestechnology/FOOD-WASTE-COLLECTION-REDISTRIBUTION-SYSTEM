import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { FoodRequestComponent } from './food-request';

describe('FoodRequestComponent', () => {
  let component: FoodRequestComponent;
  let fixture: ComponentFixture<FoodRequestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FoodRequestComponent],
      providers: [provideHttpClient(), provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(FoodRequestComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

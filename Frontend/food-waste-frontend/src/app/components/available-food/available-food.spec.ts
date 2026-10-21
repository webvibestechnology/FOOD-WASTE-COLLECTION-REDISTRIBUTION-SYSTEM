import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { AvailableFoodComponent } from './available-food';

describe('AvailableFoodComponent', () => {
  let component: AvailableFoodComponent;
  let fixture: ComponentFixture<AvailableFoodComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvailableFoodComponent],
      providers: [provideHttpClient(), provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(AvailableFoodComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

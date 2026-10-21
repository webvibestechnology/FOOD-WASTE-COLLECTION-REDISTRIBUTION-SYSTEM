import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { PickupDetailsComponent } from './pickup-details';

describe('PickupDetailsComponent', () => {
  let component: PickupDetailsComponent;
  let fixture: ComponentFixture<PickupDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PickupDetailsComponent],
      providers: [provideHttpClient(), provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(PickupDetailsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

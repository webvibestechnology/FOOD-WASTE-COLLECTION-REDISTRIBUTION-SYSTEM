import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { PickupRequestsComponent } from './pickup-requests';

describe('PickupRequestsComponent', () => {
  let component: PickupRequestsComponent;
  let fixture: ComponentFixture<PickupRequestsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PickupRequestsComponent],
      providers: [provideHttpClient(), provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(PickupRequestsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

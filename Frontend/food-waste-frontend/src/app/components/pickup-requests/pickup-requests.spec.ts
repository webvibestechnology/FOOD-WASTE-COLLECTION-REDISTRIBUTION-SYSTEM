import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PickupRequests } from './pickup-requests';

describe('PickupRequests', () => {
  let component: PickupRequests;
  let fixture: ComponentFixture<PickupRequests>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PickupRequests],
    }).compileComponents();

    fixture = TestBed.createComponent(PickupRequests);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

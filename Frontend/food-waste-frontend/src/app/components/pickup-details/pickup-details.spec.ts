import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PickupDetails } from './pickup-details';

describe('PickupDetails', () => {
  let component: PickupDetails;
  let fixture: ComponentFixture<PickupDetails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PickupDetails],
    }).compileComponents();

    fixture = TestBed.createComponent(PickupDetails);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

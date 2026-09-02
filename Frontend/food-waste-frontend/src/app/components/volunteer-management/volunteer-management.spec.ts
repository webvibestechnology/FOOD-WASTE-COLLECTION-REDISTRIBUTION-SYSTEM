import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VolunteerManagement } from './volunteer-management';

describe('VolunteerManagement', () => {
  let component: VolunteerManagement;
  let fixture: ComponentFixture<VolunteerManagement>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VolunteerManagement],
    }).compileComponents();

    fixture = TestBed.createComponent(VolunteerManagement);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

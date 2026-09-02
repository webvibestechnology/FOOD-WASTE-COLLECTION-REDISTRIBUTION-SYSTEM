import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NgoManagement } from './ngo-management';

describe('NgoManagement', () => {
  let component: NgoManagement;
  let fixture: ComponentFixture<NgoManagement>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NgoManagement],
    }).compileComponents();

    fixture = TestBed.createComponent(NgoManagement);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MgsAdmin } from './mgs-admin';

describe('MgsAdmin', () => {
  let component: MgsAdmin;
  let fixture: ComponentFixture<MgsAdmin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MgsAdmin]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MgsAdmin);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceConfirmation } from './service-confirmation.service';

describe('ServiceConfirmation', () => {
  let component: ServiceConfirmation;
  let fixture: ComponentFixture<ServiceConfirmation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ServiceConfirmation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ServiceConfirmation);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

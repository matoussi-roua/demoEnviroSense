
import { ComponentFixture, TestBed } from '@angular/core/testing';

import {DashboardService}from './dashboard.component';

describe('DashboardComponent', () => {
  let component: DashboardService;
  let fixture: ComponentFixture<DashboardService>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardService]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DashboardService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

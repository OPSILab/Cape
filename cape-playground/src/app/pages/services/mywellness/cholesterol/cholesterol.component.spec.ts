import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { CholesterolComponent } from './cholesterol.component';

describe('CholesterolComponent', () => {
  let component: CholesterolComponent;
  let fixture: ComponentFixture<CholesterolComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [CholesterolComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CholesterolComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

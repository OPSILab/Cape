import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CholesterolComponent } from './cholesterol.component';

describe('CholesterolComponent', () => {
  let component: CholesterolComponent;
  let fixture: ComponentFixture<CholesterolComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CholesterolComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CholesterolComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceLinkingComponent } from './service-linking.component';

describe('ServiceLinkingComponent', () => {
  let component: ServiceLinkingComponent;
  let fixture: ComponentFixture<ServiceLinkingComponent>;

  beforeEach(async(() => {
    void TestBed.configureTestingModule({
      declarations: [ServiceLinkingComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceLinkingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    void expect(component).toBeTruthy();
  });
});

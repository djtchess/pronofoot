import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChampionnatsComponent } from './championnats.component';

describe('ChampionnatsComponent', () => {
  let component: ChampionnatsComponent;
  let fixture: ComponentFixture<ChampionnatsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChampionnatsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChampionnatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

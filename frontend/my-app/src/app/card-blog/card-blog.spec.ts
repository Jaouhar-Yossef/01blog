import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardBlog } from './card-blog';

describe('CardBlog', () => {
  let component: CardBlog;
  let fixture: ComponentFixture<CardBlog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CardBlog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CardBlog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

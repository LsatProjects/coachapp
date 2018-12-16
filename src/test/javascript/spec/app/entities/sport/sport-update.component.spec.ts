/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { CoachappTestModule } from '../../../test.module';
import { SportUpdateComponent } from 'app/entities/sport/sport-update.component';
import { SportService } from 'app/entities/sport/sport.service';
import { Sport } from 'app/shared/model/sport.model';

describe('Component Tests', () => {
    describe('Sport Management Update Component', () => {
        let comp: SportUpdateComponent;
        let fixture: ComponentFixture<SportUpdateComponent>;
        let service: SportService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CoachappTestModule],
                declarations: [SportUpdateComponent]
            })
                .overrideTemplate(SportUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(SportUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SportService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new Sport(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.sport = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new Sport();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.sport = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.create).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));
        });
    });
});

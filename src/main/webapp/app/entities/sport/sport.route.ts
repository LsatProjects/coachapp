import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Sport } from 'app/shared/model/sport.model';
import { SportService } from './sport.service';
import { SportComponent } from './sport.component';
import { SportDetailComponent } from './sport-detail.component';
import { SportUpdateComponent } from './sport-update.component';
import { SportDeletePopupComponent } from './sport-delete-dialog.component';
import { ISport } from 'app/shared/model/sport.model';

@Injectable({ providedIn: 'root' })
export class SportResolve implements Resolve<ISport> {
    constructor(private service: SportService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Sport> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<Sport>) => response.ok),
                map((sport: HttpResponse<Sport>) => sport.body)
            );
        }
        return of(new Sport());
    }
}

export const sportRoute: Routes = [
    {
        path: 'sport',
        component: SportComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'coachappApp.sport.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'sport/:id/view',
        component: SportDetailComponent,
        resolve: {
            sport: SportResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'coachappApp.sport.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'sport/new',
        component: SportUpdateComponent,
        resolve: {
            sport: SportResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'coachappApp.sport.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'sport/:id/edit',
        component: SportUpdateComponent,
        resolve: {
            sport: SportResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'coachappApp.sport.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const sportPopupRoute: Routes = [
    {
        path: 'sport/:id/delete',
        component: SportDeletePopupComponent,
        resolve: {
            sport: SportResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'coachappApp.sport.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];

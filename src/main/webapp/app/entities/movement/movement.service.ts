import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IMovement } from 'app/shared/model/movement.model';

type EntityResponseType = HttpResponse<IMovement>;
type EntityArrayResponseType = HttpResponse<IMovement[]>;

@Injectable({ providedIn: 'root' })
export class MovementService {
    public resourceUrl = SERVER_API_URL + 'api/movements';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/movements';

    constructor(private http: HttpClient) {}

    create(movement: IMovement): Observable<EntityResponseType> {
        return this.http.post<IMovement>(this.resourceUrl, movement, { observe: 'response' });
    }

    update(movement: IMovement): Observable<EntityResponseType> {
        return this.http.put<IMovement>(this.resourceUrl, movement, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IMovement>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IMovement[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IMovement[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}

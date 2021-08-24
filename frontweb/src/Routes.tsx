import NavBar from 'components/NavBar/NavBar';
import Admin from 'pages/admin/Admin';
import Catalog from 'pages/Catalog/Catalog';
import Home from 'pages/Home/Home';
import React from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom';

const Routes = () => {
    return (
        <BrowserRouter>
            <NavBar />
            <Switch>
                <Route path='/' exact>
                    <Home />
                </Route>
                <Route path='/products'>
                    <Catalog />
                </Route>
                <Route path='/admin'>
                    <Admin/>
                </Route>
            </Switch>
        </BrowserRouter>
    );
};

export default Routes;
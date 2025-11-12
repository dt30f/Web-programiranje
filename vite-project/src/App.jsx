import {BrowserRouter as Router, Routes, Route, Navigate} from "react-router-dom";
import NavigationBar from "./components/NavigationBar";
import { Suspense} from "react";
import {Spinner} from "react-bootstrap";
import {isAuthenticated} from "./auth.js";
import LoginPage from "./pages/LoginPage.jsx";
import HomePage from "./pages/HomePage";
import MostPopularEventsPage from "./pages/MostPopularEventsPage.jsx";
import CategoriesPage from "./pages/CategoriesPage.jsx";
import {SearchProvider} from "./components/SearchContext.jsx";
import TagPage from "./pages/TagPage.jsx";
import CategoriesPageCreator from "./pages/CategoriesPageCreator.jsx";
import EventsCreatorPage from "./pages/EventsCreatorPage.jsx";
import UsersPage from "./pages/UsersPage.jsx";


const PrivateRoute = ({element}) => {
    return isAuthenticated() ? element : <Navigate to="/login"/>;
};
function App() {


    return (
        <SearchProvider>
            <div className="App">
                <Router>
                    <div className="app-container">
                        <NavigationBar />
                        <main className="main-content">
                            <Suspense
                                fallback={
                                    <Spinner animation="border" role="status">
                                        <span className="visually-hidden">Loading...</span>
                                    </Spinner>
                                }
                            >
                                <Routes>
                                    <Route path="/" element={<HomePage />} />
                                    <Route path="/tags/:tagName" element={<TagPage />} />
                                    <Route path="najposeceniji" element={<MostPopularEventsPage />} />
                                    <Route path="kategorije" element={<CategoriesPage />} />
                                    <Route path="/login" element={<LoginPage />} />
                                    <Route path="creatorKategorije" element={<PrivateRoute element={<CategoriesPageCreator/>}/> }/>
                                    <Route path="creatorEvents" element={<PrivateRoute element={<EventsCreatorPage/>}/> }/>
                                    <Route path="/korisnici" element={<PrivateRoute element={<UsersPage/>}/> }/>

                                </Routes>
                            </Suspense>
                        </main>
                    </div>
                </Router>
            </div>
        </SearchProvider>
    );
}

export default App

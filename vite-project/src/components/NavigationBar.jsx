import { Navbar, Nav, Container, Button, Form } from "react-bootstrap";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { isAuthenticated, isAdmin, isCreator, logout } from "../auth.js";
import {useContext, useEffect} from "react";
import { SearchContext } from "./SearchContext.jsx";

const NavigationBar = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const { searchTerm, setSearchTerm } = useContext(SearchContext);

    useEffect(() => {
        console.log("isADmin" + isAdmin())
        console.log("isAuth" + isAuthenticated())
        console.log("isCreator" + isCreator())
    }, []);

    const handleLogout = () => {
        logout(navigate);
    };
    return (

        <Navbar expand="lg" className="bg-body-tertiary" style={{padding: 0}}>
            <Container fluid style={{ backgroundColor: "#f3bc77"}}>
                <Navbar.Brand href="/">Event Booker</Navbar.Brand>
                <Navbar.Toggle aria-controls="navbarScroll" />
                {/*samo za obicne korisnike*/}
                {!isAuthenticated() && (
                    <>
                        <Navbar.Collapse id="navbarScroll">
                            <Nav
                                className="me-auto my-2 my-lg-0"
                                style={{ maxHeight: '100px' }}
                                navbarScroll
                            >
                                <Nav.Link as={Link} to="/" active={location.pathname === "/"}>Pocetna</Nav.Link>
                                <Nav.Link as={Link} to="/najposeceniji" active={location.pathname === "/najposeceniji"}>Najposeceniji</Nav.Link>
                                <Nav.Link as={Link} to="/kategorije" active={location.pathname === "/kategorije"}>Kategorije</Nav.Link>
                                {/* Login / Logout */}
                                {isAuthenticated() ? (
                                    <Button variant="danger" onClick={() => handleLogout()}>Logout</Button>
                                ) : (
                                    <Button variant="success" as={Link} to="/login">Login</Button>
                                )}
                            </Nav>
                            <Form className="d-flex">
                                <Form.Control
                                    type="search"
                                    placeholder="Pretraži događaje..."
                                    className="me-2"
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                />
                                <Button variant="outline-success" onClick={() => {}}>
                                    Search
                                </Button>
                            </Form>
                        </Navbar.Collapse>
                    </>
                )}
                {/*samo za kreatore*/}

                {isCreator() && (
                    <>
                        <Navbar.Collapse id="navbarScroll">
                            <Nav
                                className="me-auto my-2 my-lg-0"
                                style={{ maxHeight: '100px' }}
                                navbarScroll
                            >
                                <Nav.Link as={Link} to="/creatorKategorije" active={location.pathname === "/creatorKategorije"}>Kategorije</Nav.Link>
                                <Nav.Link as={Link} to="/creatorEvents" active={location.pathname === "/creatorEvents"}>Dogadjaji</Nav.Link>
                            </Nav>
                            <Form className="d-flex">
                                <Form.Control
                                    type="search"
                                    placeholder="Pretraži događaje..."
                                    className="me-2"
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                />
                                <Button variant="outline-success" onClick={() => {}}>
                                    Search
                                </Button>
                            </Form>
                            {isAuthenticated() ? (
                                <Button variant="danger" onClick={() => handleLogout()}>Logout</Button>
                            ) : (
                                <Button variant="success" as={Link} to="/login">Login</Button>
                            )}
                        </Navbar.Collapse>

                    </>
                )}
                {/*samo za admine*/}
                {isAdmin() && (
                    <>
                        <Navbar.Collapse id="navbarScroll">
                            <Nav
                                className="me-auto my-2 my-lg-0"
                                style={{ maxHeight: '100px' }}
                                navbarScroll
                            >
                                <Nav.Link as={Link} to="/creatorKategorije" active={location.pathname === "/creatorKategorije"}>Kategorije</Nav.Link>
                                <Nav.Link as={Link} to="/creatorEvents" active={location.pathname === "/creatorEvents"}>Dogadjaji</Nav.Link>
                                <Nav.Link as={Link} to="/korisnici" active={location.pathname=== "/korisnici"}>Korisnici</Nav.Link>
                            </Nav>
                            <Form className="d-flex">
                                <Form.Control
                                    type="search"
                                    placeholder="Pretraži događaje..."
                                    className="me-2"
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                />
                                <Button variant="outline-success" onClick={() => {}}>
                                    Search
                                </Button>
                            </Form>
                            {isAuthenticated() ? (
                                <Button variant="danger" onClick={() => handleLogout()}>Logout</Button>
                            ) : (
                                <Button variant="success" as={Link} to="/login">Login</Button>
                            )}
                        </Navbar.Collapse>
                    </>
                )}
            </Container>
        </Navbar>
    );
};

export default NavigationBar;

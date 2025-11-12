import {Col, Row, Tab, Tabs} from "react-bootstrap";
import {useContext, useEffect, useState} from "react";
import _axios from "../axiosInstance.js";
import Card from "react-bootstrap/Card";
import {SearchContext} from "./SearchContext.jsx";
import EventDetail from "./EventDetail.jsx";
import MostReactions from "./MostReactions.jsx";

const CategoryTabs = () => {
    const [categories, setCategories] = useState([])
    const [activeCategory, setActiveCategory] = useState(null);
    const [events, setEvents] = useState([]);
    const { searchTerm } = useContext(SearchContext);
    const [showModal, setShowModal] = useState(false);
    const [selectedEventId, setSelectedEventId] = useState(null);


    useEffect(() => {
        const fetchCategories = async () =>{
            try{
                const response = await _axios("/api/category")
                console.log(response)
                setCategories(response.data)
            }catch (error){
                console.log("Greška prilikom učitavanja eventa", error)
            }
        }
        fetchCategories()
    }, []);

    const handleSelect = async (category) => {
        setActiveCategory(category);
        try {
            const response = await _axios(`/api/events/category?category=${category}`);
            setEvents(response.data);
        } catch (error) {
            console.log("Greška prilikom učitavanja eventa", error);
        }
    };
    const filteredEvents = events.filter(
        (event) =>
            event.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
            event.description.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return(
        <>
            <EventDetail
                show={showModal}
                onClose={() => setShowModal(false)}
                eventId={selectedEventId}
            />
            <div
                style={{
                    position: "fixed",
                    top: "90px",
                    right: "30px",
                    zIndex: 9999
                }}
            >
                <MostReactions onEventClick={(eventId) => {
                    setSelectedEventId(eventId)
                    setShowModal(true)
                }}/>
            </div>
            <Tabs
                activeKey={activeCategory}
                onSelect={handleSelect}
                style={{backgroundColor: "#402a23"}}
                className="mb-3"
            >
                {categories.map((category) => (
                    <Tab
                        key={category}
                        eventKey={category}
                        title={
                            <span style={{color: activeCategory === category ? "black" : "white"}}>
                                {category}
                            </span>
                        }
                    >
                        <Row className="g-3">
                            {filteredEvents.map((event) => (
                                <Col key={event.id} xs={12} className="d-flex justify-content-center">
                                    <Card className="text-center" style={{width: "400px", backgroundColor: "#f3bc77"}}>
                                        <Card.Header>{event.category}</Card.Header>
                                        <Card.Body>
                                            <Card.Title onClick={() => {
                                                setSelectedEventId(event.id);
                                                setShowModal(true)
                                            }
                                            }>{event.title}</Card.Title>
                                            <Card.Text>
                                                {event.description}
                                            </Card.Text>
                                            {/*<Button variant="primary">Go somewhere</Button>*/}
                                        </Card.Body>
                                    </Card>
                                </Col>
                            ))}
                        </Row>
                    </Tab>
                ))}
            </Tabs>
        </>
    )
}
export default CategoryTabs
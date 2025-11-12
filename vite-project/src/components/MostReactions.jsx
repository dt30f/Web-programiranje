import {useEffect, useState} from "react";
import _axios from "../axiosInstance.js";
import {ListGroup} from "react-bootstrap";
import Card from "react-bootstrap/Card";

const MostReactions = ({ onEventClick }) =>{
    const [popularEvents, setPopularEvents] = useState([])

    useEffect(() => {
        const fetchInfo = async () =>{
            const res = await _axios(`/api/events/most_reactions`)
            setPopularEvents(res.data)
        }
        fetchInfo()
    }, []);

    const openDetails = (eventId) =>{
        if(onEventClick){
            onEventClick(eventId);
        }
    }

    return(
        <div>
            {popularEvents.map((event) =>(
                <Card style={{ width: '18rem', padding: "5px", backgroundColor: "#402a23"}}>
                    <ListGroup variant="flush">
                        <ListGroup.Item onClick={() => openDetails(event.id)} style={{backgroundColor: "#a55233"}}>{event.title}</ListGroup.Item>
                        <ListGroup.Item style={{backgroundColor: "#f3bc77"}}>{event.description}</ListGroup.Item>
                        <ListGroup.Item style={{backgroundColor: "#f3bc77"}}>Reakcije: {event.like + event.dislike}</ListGroup.Item>
                    </ListGroup>
                </Card>
            ))}
        </div>
    )
}
export default MostReactions
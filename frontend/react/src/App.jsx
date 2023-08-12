import {Wrap, WrapItem, Spinner, Text} from '@chakra-ui/react';
import SidebarWithHeader from "./components/shared/SideBar.jsx";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";
import CardWithImage from "./components/Card.jsx";

const App = () => {

    const [customers, setCustomers]  = useState([])
    const [loading, setLoading] = useState(false)

    useEffect(()=> {
        setLoading(true)
        getCustomers().then(res => {
            setCustomers(res.data)
        }).catch(err => {
            console.log(err)
        }).finally(
            () => {
                setLoading(false)
            }
        )
    },[])

    if (loading){
        return (
        <SidebarWithHeader>
            <Spinner
            thickness='4px'
            speed='0.65s'
            emptyColor='gray.200'
            color='blue.500'
            size={'xl'}
            />
        </SidebarWithHeader>
        )
    }

    if (customers.length <= 0) {
        return(
            <SidebarWithHeader>
                <Text> No Customers Available </Text>
            </SidebarWithHeader>
        )
    }

    return(
        <SidebarWithHeader>
            <Wrap justify='center' spacing='20px'>
                {customers.map((customer, index) => (
                    <WrapItem key={index}>
<<<<<<< HEAD
                        <CardWithImage {...customer}/>
=======
                        <CardWithImage
                            {...customer}
                            RANDOM = {index}/>
>>>>>>> 6064df5c7148c0dcde8ee1efc67ba55922431e65
                    </WrapItem>
                ))}
            </Wrap>

        </SidebarWithHeader>
    )
}

export default App;
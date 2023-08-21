'use client'

import { useState, useRef } from 'react'
import {
    Box,
    Heading,
    Text,
    Img,
    Flex,
    Center,
    useColorModeValue,
    HStack,
    useDisclosure,
    AlertDialog,
    AlertDialogOverlay,
    AlertDialogContent,
    AlertDialogHeader,
    AlertDialogBody,
    AlertDialogFooter, Button,
} from '@chakra-ui/react'

import { BsArrowUpRight, BsHeartFill, BsHeart } from 'react-icons/bs'
import {customerProfilePictureUrl, deleteCustomer} from "../../services/client.js";
import {errorNotification, successNotification} from "../../services/Notification.js";
import UpdateCustomerDrawer from "./UpdateCustomerDrawer.jsx";


export default function CardWithImage({id, name, email, age, gender, RANDOM, fetchCustomers}) {

    const { isOpen, onOpen, onClose } = useDisclosure()
    const cancelRef = useRef()

    const [liked, setLiked] = useState(false)

    const SEX = gender === "MALE" ? "men" : "women"

    return (
        <Center py={6}>
            <Box
                w="2xs"
                rounded={'sm'}
                my={5}
                mx={[0, 5]}
                overflow={'hidden'}
                bg="white"
                border={'1px'}
                borderColor="black"
                boxShadow={useColorModeValue('6px 6px 0 black', '6px 6px 0 cyan')}>
                <Box h={'200px'} borderBottom={'1px'} borderColor="black">
                    <Img
                        src=
                        {customerProfilePictureUrl(id)}
                        roundedTop={'sm'}
                        objectFit="cover"
                        h="full"
                        w="full"
                        alt={'Blog Image'}
                    />
                </Box>
                <Box p={4}>
                    <Box bg="black" display={'inline-block'} px={2} py={1} color="white" mb={2}>
                        <Text fontSize={'xs'} fontWeight="medium">
                            {age}
                        </Text>
                    </Box>
                    <Heading color={'black'} fontSize={'2xl'} noOfLines={1}>
                        ID : {RANDOM+1} - {name}
                    </Heading>
                    <Text color={'gray.500'} noOfLines={2}>
                        {email}
                    </Text>
                </Box>
                <HStack borderTop={'1px'} color="black">
                    <Flex
                        onClick={onOpen}
                        p={4}
                        alignItems="center"
                        justifyContent={'space-between'}
                        roundedBottom={'sm'}
                        cursor={'pointer'}
                        color={"red.400"}
                        w="full"
                        _hover={{
                            transform: 'transformY(-2px)',
                            boxShadow: 'dark-lg'
                        }}>
                        <Text fontSize={'md'} fontWeight={'semibold'}>
                            Delete
                        </Text>
                        <BsArrowUpRight />
                    </Flex>

                    <AlertDialog
                        isOpen={isOpen}
                        leastDestructiveRef={cancelRef}
                        onClose={onClose}
                    >
                        <AlertDialogOverlay>
                            <AlertDialogContent>
                                <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                                    Delete Customer
                                </AlertDialogHeader>

                                <AlertDialogBody>
                                    Are you sure you want to delete {name}? You can't undo this action afterwards.
                                </AlertDialogBody>

                                <AlertDialogFooter>
                                    <Button ref={cancelRef} onClick={onClose}>
                                        Cancel
                                    </Button>
                                    <Button colorScheme='red' onClick={() => {

                                        deleteCustomer(id).then(res => {
                                            console.log(res)
                                            successNotification(
                                                "Customer deleted",
                                                `${name} was successfully deleted`
                                            )
                                            fetchCustomers();
                                        }).catch(err => {
                                                console.log(err)
                                                errorNotification(
                                                    err.code,
                                                    err.response.data.message
                                                )
                                            }).finally( () => {
                                                onClose()
                                        })

                                    }} ml={3}>
                                        Delete
                                    </Button>
                                </AlertDialogFooter>
                            </AlertDialogContent>
                        </AlertDialogOverlay>
                    </AlertDialog>

                    <UpdateCustomerDrawer initialValues = {{ name, email, age }}
                                          customerId = {id}
                                          fetchCustomers={fetchCustomers}
                    />

                </HStack>
            </Box>
        </Center>
    )
}
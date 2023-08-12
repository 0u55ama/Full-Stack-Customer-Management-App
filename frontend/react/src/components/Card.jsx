'use client'

import { useState } from 'react'
import {
    Box,
    Heading,
    Text,
    Img,
    Flex,
    Center,
    useColorModeValue,
    HStack,
} from '@chakra-ui/react'
import { BsArrowUpRight, BsHeartFill, BsHeart } from 'react-icons/bs'

<<<<<<< HEAD
export default function CardWithImage({id, name, email, age}) {

    const [liked, setLiked] = useState(false)

=======
export default function CardWithImage({id, name, email, age, gender, RANDOM}) {

    const [liked, setLiked] = useState(false)

    const SEX = gender === "MALE" ? "men" : "women"

>>>>>>> 6064df5c7148c0dcde8ee1efc67ba55922431e65
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
                        src={
<<<<<<< HEAD
                            'https://images.unsplash.com/photo-1542435503-956c469947f6?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80'
                        }
=======
                            `https://randomuser.me/api/portraits/${SEX}/${RANDOM}.jpg`
                    }
>>>>>>> 6064df5c7148c0dcde8ee1efc67ba55922431e65
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
                        ID : {id} - {name}
                    </Heading>
                    <Text color={'gray.500'} noOfLines={2}>
                        {email}
                    </Text>
                </Box>
                <HStack borderTop={'1px'} color="black">
                    <Flex
                        p={4}
                        alignItems="center"
                        justifyContent={'space-between'}
                        roundedBottom={'sm'}
                        cursor={'pointer'}
                        w="full">
                        <Text fontSize={'md'} fontWeight={'semibold'}>
                            Explore Profile
                        </Text>
                        <BsArrowUpRight />
                    </Flex>

                </HStack>
            </Box>
        </Center>
    )
}
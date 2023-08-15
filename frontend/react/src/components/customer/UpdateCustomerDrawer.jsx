import {
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton, DrawerContent,
    DrawerFooter,
    DrawerHeader, DrawerOverlay, Flex, Text,
    useDisclosure
} from "@chakra-ui/react";
import UpdateCustomerForm from "./UpdateCustomerForm.jsx";
import {BsArrowUpRight} from "react-icons/bs";

const AddIcon = () => "+";
const UpdateCustomerDrawer = ({ fetchCustomers, initialValues , customerId}) => {
    const {isOpen, onOpen, onClose} = useDisclosure()

    return (
        <>
            <Flex
                onClick={onOpen}

                p={4}
                alignItems="center"
                justifyContent={'space-between'}
                roundedBottom={'sm'}
                cursor={'pointer'}
                color={"green.400"}
                w="full"
                _hover={{
                    transform: 'transformY(-2px)',
                    boxShadow: 'dark-lg'

                }}>

                <Text fontSize={'md'} fontWeight={'semibold'}>
                    Update
                </Text>
                <BsArrowUpRight />
            </Flex>

            <Drawer isOpen={isOpen} onClose={onClose} size={"xl"}>
                <DrawerOverlay/>
                <DrawerContent>
                    <DrawerCloseButton/>
                    <DrawerHeader>Update {name}</DrawerHeader>

                    <DrawerBody>
                        <UpdateCustomerForm
                            fetchCustomers = {fetchCustomers}
                            initialValues={initialValues}
                            customerId={customerId}
                        />
                    </DrawerBody>
                    <DrawerFooter>
                    </DrawerFooter>
                </DrawerContent>
            </Drawer>

        </>
    );



};

export default UpdateCustomerDrawer;


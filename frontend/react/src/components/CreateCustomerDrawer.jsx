import {
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton, DrawerContent,
    DrawerFooter,
    DrawerHeader, DrawerOverlay,
    useDisclosure
} from "@chakra-ui/react";
import CreateCustomerForm from "./CreateCustomerForm.jsx";

const AddIcon = () => "+";
const CreateCustomerDrawer = ({ fetchCustomers }) => {
    const {isOpen, onOpen, onClose} = useDisclosure()

    return (
        <>
            <Button
                leftIcon={<AddIcon/>}
                colorScheme={"gray"}
                onClick={onOpen}
            >
                Create customer
            </Button>

            <Drawer isOpen={isOpen} onClose={onClose} size={"xl"}>
                <DrawerOverlay/>
                <DrawerContent>
                    <DrawerCloseButton/>
                    <DrawerHeader>Create New Customer</DrawerHeader>

                    <DrawerBody>
                        <CreateCustomerForm
                            fetchCustomers = {fetchCustomers}
                        />
                    </DrawerBody>

                    <DrawerFooter>
                    </DrawerFooter>
                </DrawerContent>
            </Drawer>

        </>
    );



};

export default CreateCustomerDrawer;


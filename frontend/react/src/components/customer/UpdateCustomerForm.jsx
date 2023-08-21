import { Formik, Form, useField,} from 'formik';
import * as Yup from 'yup';
import {Alert, Image,AlertIcon, Box, Button, FormLabel, Input, Select, Stack, VStack} from "@chakra-ui/react";
import {
    customerProfilePictureUrl,
    saveCustomer,
    updateCustomer,
    uploadCustomerProfilePicture
} from "../../services/client.js";
import {errorNotification, successNotification} from "../../services/Notification.js";
import {useCallback} from "react";
import {useDropzone} from "react-dropzone";


const MyTextInput = ({ label, ...props }) => {

    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={2}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const MyDropzone = ({ customerId, fetchCustomers }) => {
    const onDrop = useCallback(acceptedFiles => {
        const formData = new FormData();
        formData.append("file", acceptedFiles[0])

        uploadCustomerProfilePicture(
            customerId,
            formData
        ).then(() => {
            successNotification("Success", "Profile picture uploaded")
            fetchCustomers()
        }).catch(() => {
            errorNotification("Error", "Profile picture failed upload")
        })
    }, [])
    const {getRootProps, getInputProps, isDragActive} = useDropzone({onDrop})

    return (
        <Box {...getRootProps()}
             w={'100%'}
             textAlign={'center'}
             border={'dashed'}
             borderColor={'gray.200'}
             borderRadius={'3xl'}
             p={6}
             rounded={'md'}>
            <input {...getInputProps()} />
            {
                isDragActive ?
                    <p>Drop the picture here ...</p> :
                    <p>Drag 'n' drop picture here, or click to select picture</p>
            }
        </Box>
    )
}


// And now we can use these
const UpdateCustomerForm = ({ fetchCustomers, initialValues, customerId }) => {
    return (
        <>
            <VStack spacing={'5'} mb={'5'}>
                <Image
                    borderRadius={'full'}
                    boxSize={'150px'}
                    objectFit={'cover'}
                    src={customerProfilePictureUrl(customerId)}
                />
                <MyDropzone
                    customerId={customerId}
                    fetchCustomers={fetchCustomers}
                />
            </VStack>
            <Formik
                initialValues={initialValues}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(15, 'Must be 15 characters or less')
                        .required('Required'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    age: Yup.number()
                        .min(16, 'Must be at least 16 years of age')
                        .max(100, 'Must be at most 100 years of age')
                        .required('Required'),
                })}
                onSubmit={(updatedCustomer, { setSubmitting }) => {
                    setSubmitting(true)
                    updateCustomer( customerId, updatedCustomer)
                        .then(res => {
                        console.log(res);
                        successNotification(
                            "Customer saved",
                            `${updatedCustomer.name} was successfully updated`
                        )
                        fetchCustomers();

                    }).catch(err => {
                        console.log(err);
                        errorNotification(
                            err.code,
                            err.response.data.message
                        )
                    }).finally(() => {
                        setSubmitting(false)
                    })

                }}
            >
                { ({ isValid, isSubmitting, dirty}) => (
                    <Form>
                        <Stack spacing={"24px"}>
                            <MyTextInput
                                label="Name"
                                name="name"
                                type="text"
                                placeholder="Oussama"
                            />

                            <MyTextInput
                                label="Email Address"
                                name="email"
                                type="email"
                                placeholder="osm@mail.com"
                            />

                            <MyTextInput
                                label="Age"
                                name="age"
                                type="number"
                                placeholder="22"
                            />

                            <Button disabled={!(isValid && dirty) || isSubmitting} type="submit">Submit</Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};

export default UpdateCustomerForm;
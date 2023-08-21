'use client'

import {
    Alert,
    AlertIcon,
    Box,
    Button,
    Flex,
    FormLabel,
    Heading,
    Image,
    Input,
    Link,
    Stack,
    Text,
} from '@chakra-ui/react'
import {Form, Formik, useField} from "formik";
import * as Yup from "yup"
import {useAuth} from "../context/AuthContext.jsx";
import {errorNotification} from "../../services/Notification.js";
import {useNavigate} from "react-router-dom";
import {useEffect} from "react";


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

const LoginForm = () => {
    const navigate = useNavigate()
    const { login } = useAuth();

    return (
        <Formik

            validateOnMount={true}
            validationSchema={
                Yup.object({
                    username: Yup.string()
                        .email("Must be valid email")
                        .required("Email is required"),
                    password: Yup.string()
                        .max(20, "Password cannot be more that 20 characters")
                        .required("Password is required")

            })
            }
            initialValues={{username: '', password: ''}}
            onSubmit={(values, {setSubmitting}) => {
                setSubmitting(true)
                login(values).then(res => {
                    //TODO: navigate to the dashoard
                    navigate("/dashboard")
                    console.log("Successfully logged in ")
                }).catch(err => {
                    errorNotification(
                        err.code,
                        err.response.data.message
                    )
                }).finally(() => {
                    setSubmitting(false)
                })
            }}>

            {({isValid, isSubmitting}) => (
                <Form>
                    <Stack mt={15} spacing={15}>
                        <MyTextInput
                            label={"Email"}
                            name={"username"}
                            type={"email"}
                            placeholder={"hello@mail.com"}
                        />
                        <MyTextInput
                            label={"Password"}
                            name={"password"}
                            type={"password"}
                            placeholder={"Type your password"}
                        />

                        <Button
                            type={"submit"}
                            disabled={!isValid || isSubmitting}>
                            Login
                        </Button>
                    </Stack>
                </Form>
            )}
        </Formik>
    )
}

const Login = () => {

    const { customer } = useAuth()
    const navigate = useNavigate()

    useEffect(() => {
        if (customer){
            navigate("/dashboard/customers")
        }
    })

    return (
        <Stack minH={'100vh'} direction={{ base: 'column', md: 'row' }}>
            <Flex p={8} flex={1} align={'center'} justify={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Heading fontSize={'2xl'}>Sign in to your account</Heading>
                    <LoginForm/>
                    <Link color={"blue.500"} href={"/signup"}>
                        Don't have an account!? SignUp now.
                    </Link >
                </Stack>
            </Flex>
            <Flex flex={1} p={10} flexDirection={"column"} alignItems={"center"} justifyContent={"center"} bgGradient={{sm: 'linear(to-r, blue.600, purple.600)'}}>
                <Text fontSize={"6xl"} color={"white"} fontWeight={"bold"} mb={5}>
                    <Link target={"_blank"} href={"https://oussamaaitlamaalam.com"}>
                        Porfolio
                    </Link>
                </Text>

            </Flex>
        </Stack>
    )
}

export default Login;
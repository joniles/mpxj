# How To: Read Primavera P6 Web Services

Primavera P6 Web Services (p6ws) provides SOAP and REST APIs to allow you to
access data held in Primavera P6. p6ws is often installed as part of the
Primavera P6 Enterprise Project Portfolio Management (EPPM) product. You may
also find that it is available if you are using Oracle-hosted Primavera P6
Professional. Third party P6 hosting services may also provide access to p6ws.

## Authorization

You will need to use a P6 user account to access the APIs exposed by p6ws. Best
practice suggests creating a user account dedicated to this purpose, although a
normal user account can be used. You will need to ensure that the user account
is authorized to use Web Services. This setting can be found in the EPPM web
interface on the Administration tab under User Administration. The Module
Access tab at the bottom of the page lists Web Services as one of the available
modules.

The user account will also need to have access to the projects you wish to work
with via the API. At minimum the user account will need read access, including
the Export Project Data" privilege.

## Authentication

The p6ws REST APIs allow for the use of Basic Authentication and OAuth
authentication mechanisms. Note that the API documentation indicates that OAuth
is only supported for p6ws hosted on Oracle Cloud Infrastructure. The type of
authentication used is selected when you create an instance of the reader
class, as illustrated by the examples in the sections below.

> NOTE: I have not had the opportunity to test OAuth authentication with p6ws.
> The MPXJ reader class allows you to pass a Bearer token, which assumes that
> you have managed the authentication process yourself. Please 
> [get in touch](mailto:jon@timephased.com)
> if you are using or need to use OAuth as I'd like to improve MPXJ's 
> support in this area.

## List Projects

## Read a Project

## Export a Project



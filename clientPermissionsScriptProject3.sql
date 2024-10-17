grant select on bikedb.* to client1;
grant select, update on bikedb.* to client2;
grant select on project3.* to client1;
grant select, update on project3.* to client2;
grant select, insert, update on operationslog.* to project3app;
grant select on operationslog.* to theaccountant;
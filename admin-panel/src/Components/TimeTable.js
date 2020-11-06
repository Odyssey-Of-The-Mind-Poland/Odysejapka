import React from "react";
import { List, Datagrid, TextField, EditButton, DeleteButton } from 'react-admin'

const TimeTable = (props) => {
    console.log(props)
    return(
        <List {...props}>
            <Datagrid>
                <TextField source="id"/>
                <TextField source="city"/>
                <TextField source="team"/>
                <TextField source="problem"/>
                <TextField source="age"/>
                <TextField source="stage"/>
                <TextField source="performance"/>
                <TextField source="spontan"/>
            </Datagrid>
        </List>
    )
}

export default TimeTable
import * as React from "react";
import { render } from 'react-dom';
import { Admin, Resource } from 'react-admin';
import dataProvider from './dataProvider';
import authProvider from "./authProvider";
import TimeTable from "./Components/TimeTable";

function App() {
  return (
      <Admin dataProvider={dataProvider}
             authProvider={authProvider}>
          <Resource name="timeTable" list={TimeTable} />
      </Admin>
  );
}

export default App;

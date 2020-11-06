import logo from './logo.svg';
import * as React from "react";
import { render } from 'react-dom';
import { Admin, Resource } from 'react-admin';
import dataProvider from './dataProvider';
import TimeTable from "./Components/TimeTable";

function App() {
  return (
      <Admin dataProvider={dataProvider}>
          <Resource name="timeTable" list={TimeTable} />
      </Admin>
  );
}

export default App;

pragma solidity >=0.5.16;

contract BasicString{
    string private str;

    constructor() public{
        str = "Hello World";
    }

    function get() public view returns(string memory) {
        return str;
    }

    function set(string memory _str) public{
        str = _str;
    }
}
